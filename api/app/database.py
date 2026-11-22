"""Configuración de la conexión con la base de datos SQLite."""

from collections.abc import Generator
from typing import Annotated

from fastapi import Depends
from sqlmodel import Session, SQLModel, create_engine

DATABASE_URL = "sqlite:///./vetcare.db"

engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})


def crear_tablas() -> None:
    """Crea las tablas definidas en los modelos si aún no existen."""
    SQLModel.metadata.create_all(engine)


def get_session() -> Generator[Session, None, None]:
    """Entrega una sesión por petición y la cierra al terminar."""
    with Session(engine) as session:
        yield session


SessionDep = Annotated[Session, Depends(get_session)]