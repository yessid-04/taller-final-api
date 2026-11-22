"""Endpoints del recurso mascotas."""

from typing import Annotated

from fastapi import APIRouter, HTTPException, Query, status
from sqlmodel import Session, select

from app.database import SessionDep
from app.models import Dueno, Mascota, MascotaCreate, MascotaPublic

router = APIRouter(prefix="/mascotas", tags=["Mascotas"])


def obtener_mascota_o_404(mascota_id: int, session: Session) -> Mascota:
    """Busca una mascota por id o responde 404 si no existe."""
    mascota = session.get(Mascota, mascota_id)
    if mascota is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No existe la mascota con id {mascota_id}",
        )
    return mascota


def validar_dueno_existente(dueno_id: int, session: Session) -> None:
    """Responde 404 si el dueño indicado para la mascota no existe."""
    if session.get(Dueno, dueno_id) is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No existe el dueño con id {dueno_id}",
        )


@router.get("", response_model=list[MascotaPublic], summary="Listar mascotas")
def listar_mascotas(
    session: SessionDep,
    offset: Annotated[int, Query(ge=0)] = 0,
    limit: Annotated[int, Query(ge=1, le=100)] = 20,
):
    """Devuelve las mascotas de forma paginada."""
    return session.exec(select(Mascota).offset(offset).limit(limit)).all()


@router.get(
    "/{mascota_id}", response_model=MascotaPublic, summary="Consultar una mascota"
)
def consultar_mascota(mascota_id: int, session: SessionDep):
    """Devuelve una mascota por su id."""
    return obtener_mascota_o_404(mascota_id, session)


@router.post(
    "",
    response_model=MascotaPublic,
    status_code=status.HTTP_201_CREATED,
    summary="Crear una mascota",
)
def crear_mascota(datos: MascotaCreate, session: SessionDep):
    """Registra una mascota asociada a un dueño existente."""
    validar_dueno_existente(datos.dueno_id, session)
    mascota = Mascota.model_validate(datos)
    session.add(mascota)
    session.commit()
    session.refresh(mascota)
    return mascota


@router.put(
    "/{mascota_id}", response_model=MascotaPublic, summary="Reemplazar una mascota"
)
def actualizar_mascota(mascota_id: int, datos: MascotaCreate, session: SessionDep):
    """Reemplaza todos los datos de una mascota existente."""
    mascota = obtener_mascota_o_404(mascota_id, session)
    validar_dueno_existente(datos.dueno_id, session)
    mascota.sqlmodel_update(datos.model_dump())
    session.add(mascota)
    session.commit()
    session.refresh(mascota)
    return mascota


@router.delete(
    "/{mascota_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Eliminar una mascota",
)
def eliminar_mascota(mascota_id: int, session: SessionDep) -> None:
    """Elimina una mascota, siempre que no tenga citas asociadas."""
    mascota = obtener_mascota_o_404(mascota_id, session)
    if mascota.citas:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede eliminar una mascota que tiene citas registradas",
        )
    session.delete(mascota)
    session.commit()