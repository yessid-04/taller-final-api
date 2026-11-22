"""Punto de entrada de la API de VetCare."""

from collections.abc import AsyncGenerator
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.database import crear_tablas
from app.routers import citas, duenos, mascotas


@asynccontextmanager
async def lifespan(_: FastAPI) -> AsyncGenerator[None, None]:
    """Prepara la base de datos antes de recibir peticiones."""
    crear_tablas()
    yield


app = FastAPI(
    title="API VetCare",
    description="API REST para gestionar dueños, mascotas y citas de una clínica veterinaria.",
    version="1.0.0",
    lifespan=lifespan,
)

app.include_router(duenos.router)
app.include_router(mascotas.router)
app.include_router(citas.router)