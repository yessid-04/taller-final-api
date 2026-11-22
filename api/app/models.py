"""Modelos de tabla y esquemas de entrada y salida de VetCare."""

from datetime import date

from pydantic import field_validator
from sqlmodel import Field, Relationship, SQLModel


# ---------- DUEÑO ----------
class DuenoBase(SQLModel):
    """Atributos comunes de un dueño."""

    nombre: str = Field(min_length=2, max_length=100)
    documento: str = Field(min_length=5, max_length=15)
    telefono: str = Field(min_length=7, max_length=15)
    email: str = Field(min_length=5, max_length=100)


class Dueno(DuenoBase, table=True):
    """Tabla de dueños."""

    id: int | None = Field(default=None, primary_key=True)
    mascotas: list["Mascota"] = Relationship(back_populates="dueno")


class DuenoCreate(DuenoBase):
    """Datos que el cliente envía para crear o reemplazar un dueño."""


class DuenoPublic(DuenoBase):
    """Datos de un dueño que la API devuelve."""

    id: int


# ---------- MASCOTA ----------
class MascotaBase(SQLModel):
    """Atributos comunes de una mascota."""

    nombre: str = Field(min_length=2, max_length=50)
    especie: str = Field(min_length=2, max_length=30)
    raza: str = Field(min_length=2, max_length=50)
    edad: int = Field(ge=0, le=40, description="Edad en años (redondeada)")
    peso: float = Field(gt=0, le=200, description="Peso en kilogramos")
    dueno_id: int = Field(foreign_key="dueno.id")


class Mascota(MascotaBase, table=True):
    """Tabla de mascotas. Cada mascota pertenece a un dueño."""

    id: int | None = Field(default=None, primary_key=True)
    dueno: Dueno | None = Relationship(back_populates="mascotas")
    citas: list["Cita"] = Relationship(back_populates="mascota")


class MascotaCreate(MascotaBase):
    """Datos que el cliente envía para crear o reemplazar una mascota."""


class MascotaPublic(MascotaBase):
    """Datos de una mascota que la API devuelve."""

    id: int


# ---------- CITA ----------
ESTADOS_VALIDOS = ("pendiente", "atendida", "cancelada")


class CitaBase(SQLModel):
    """Atributos comunes de una cita."""

    fecha: date
    motivo: str = Field(min_length=3, max_length=200)
    veterinario: str = Field(min_length=2, max_length=100)
    estado: str = Field(default="pendiente", max_length=20)
    costo: float = Field(ge=0)
    mascota_id: int = Field(foreign_key="mascota.id")

    @field_validator("estado")
    @classmethod
    def validar_estado(cls, valor: str) -> str:
        """Acepta solo los estados permitidos; si no, responde 422."""
        if valor not in ESTADOS_VALIDOS:
            raise ValueError("El estado debe ser pendiente, atendida o cancelada")
        return valor


class Cita(CitaBase, table=True):
    """Tabla de citas. Cada cita pertenece a una mascota."""

    id: int | None = Field(default=None, primary_key=True)
    mascota: Mascota | None = Relationship(back_populates="citas")


class CitaCreate(CitaBase):
    """Datos que el cliente envía para crear o reemplazar una cita."""


class CitaPublic(CitaBase):
    """Datos de una cita que la API devuelve."""

    id: int