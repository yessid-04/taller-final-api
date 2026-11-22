"""Endpoints del recurso dueños."""

from typing import Annotated

from fastapi import APIRouter, HTTPException, Query, status
from sqlmodel import Session, select

from app.database import SessionDep
from app.models import Dueno, DuenoCreate, DuenoPublic, Mascota, MascotaPublic

router = APIRouter(prefix="/duenos", tags=["Dueños"])


def obtener_dueno_o_404(dueno_id: int, session: Session) -> Dueno:
    """Busca un dueño por id o responde 404 si no existe."""
    dueno = session.get(Dueno, dueno_id)
    if dueno is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No existe el dueño con id {dueno_id}",
        )
    return dueno


@router.get("", response_model=list[DuenoPublic], summary="Listar dueños")
def listar_duenos(
    session: SessionDep,
    offset: Annotated[int, Query(ge=0)] = 0,
    limit: Annotated[int, Query(ge=1, le=100)] = 20,
):
    """Devuelve los dueños de forma paginada."""
    return session.exec(select(Dueno).offset(offset).limit(limit)).all()


@router.get("/{dueno_id}", response_model=DuenoPublic, summary="Consultar un dueño")
def consultar_dueno(dueno_id: int, session: SessionDep):
    """Devuelve un dueño por su id."""
    return obtener_dueno_o_404(dueno_id, session)


@router.get(
    "/{dueno_id}/mascotas",
    response_model=list[MascotaPublic],
    summary="Listar las mascotas de un dueño",
)
def listar_mascotas_de_dueno(dueno_id: int, session: SessionDep):
    """Devuelve las mascotas que pertenecen a un dueño."""
    obtener_dueno_o_404(dueno_id, session)
    return session.exec(select(Mascota).where(Mascota.dueno_id == dueno_id)).all()


@router.post(
    "",
    response_model=DuenoPublic,
    status_code=status.HTTP_201_CREATED,
    summary="Crear un dueño",
)
def crear_dueno(datos: DuenoCreate, session: SessionDep):
    """Registra un dueño nuevo."""
    dueno = Dueno.model_validate(datos)
    session.add(dueno)
    session.commit()
    session.refresh(dueno)
    return dueno


@router.put("/{dueno_id}", response_model=DuenoPublic, summary="Reemplazar un dueño")
def actualizar_dueno(dueno_id: int, datos: DuenoCreate, session: SessionDep):
    """Reemplaza todos los datos de un dueño existente."""
    dueno = obtener_dueno_o_404(dueno_id, session)
    dueno.sqlmodel_update(datos.model_dump())
    session.add(dueno)
    session.commit()
    session.refresh(dueno)
    return dueno


@router.delete(
    "/{dueno_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Eliminar un dueño",
)
def eliminar_dueno(dueno_id: int, session: SessionDep) -> None:
    """Elimina un dueño, siempre que no tenga mascotas asociadas."""
    dueno = obtener_dueno_o_404(dueno_id, session)
    if dueno.mascotas:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede eliminar un dueño que tiene mascotas registradas",
        )
    session.delete(dueno)
    session.commit()