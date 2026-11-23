"""Endpoints del recurso citas."""

from typing import Annotated

from fastapi import APIRouter, HTTPException, Query, status
from sqlmodel import Session, select

from app.database import SessionDep
from app.models import ESTADOS_VALIDOS, Cita, CitaCreate, CitaPublic, Mascota

router = APIRouter(prefix="/citas", tags=["Citas"])

PATRON_ESTADO = "^(" + "|".join(ESTADOS_VALIDOS) + ")$"


def obtener_cita_o_404(cita_id: int, session: Session) -> Cita:
    """Busca una cita por id o responde 404 si no existe."""
    cita = session.get(Cita, cita_id)
    if cita is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No existe la cita con id {cita_id}",
        )
    return cita


def validar_mascota_existente(mascota_id: int, session: Session) -> None:
    """Responde 404 si la mascota indicada para la cita no existe."""
    if session.get(Mascota, mascota_id) is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"No existe la mascota con id {mascota_id}",
        )


@router.get("", response_model=list[CitaPublic], summary="Listar y filtrar citas")
def listar_citas(
    session: SessionDep,
    estado: Annotated[
        str | None,
        Query(
            pattern=PATRON_ESTADO,
            description="Filtra por estado: pendiente, atendida o cancelada",
        ),
    ] = None,
    offset: Annotated[int, Query(ge=0)] = 0,
    limit: Annotated[int, Query(ge=1, le=100)] = 20,
):
    """Devuelve las citas; permite filtrar por estado."""
    consulta = select(Cita)
    if estado is not None:
        consulta = consulta.where(Cita.estado == estado)
    return session.exec(consulta.offset(offset).limit(limit)).all()


@router.get("/{cita_id}", response_model=CitaPublic, summary="Consultar una cita")
def consultar_cita(cita_id: int, session: SessionDep):
    """Devuelve una cita por su id."""
    return obtener_cita_o_404(cita_id, session)


@router.post(
    "",
    response_model=CitaPublic,
    status_code=status.HTTP_201_CREATED,
    summary="Crear una cita",
)
def crear_cita(datos: CitaCreate, session: SessionDep):
    """Registra una cita asociada a una mascota existente."""
    validar_mascota_existente(datos.mascota_id, session)
    cita = Cita.model_validate(datos)
    session.add(cita)
    session.commit()
    session.refresh(cita)
    return cita


@router.put("/{cita_id}", response_model=CitaPublic, summary="Reemplazar una cita")
def actualizar_cita(cita_id: int, datos: CitaCreate, session: SessionDep):
    """Reemplaza todos los datos de una cita existente."""
    cita = obtener_cita_o_404(cita_id, session)
    validar_mascota_existente(datos.mascota_id, session)
    cita.sqlmodel_update(datos.model_dump())
    session.add(cita)
    session.commit()
    session.refresh(cita)
    return cita


@router.delete(
    "/{cita_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Eliminar una cita",
)
def eliminar_cita(cita_id: int, session: SessionDep) -> None:
    """Elimina una cita por su id."""
    cita = obtener_cita_o_404(cita_id, session)
    session.delete(cita)
    session.commit()
