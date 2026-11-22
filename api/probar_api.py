"""Prueba automática de la API de VetCare.

Uso (con la API encendida, desde la carpeta api):
    python probar_api.py
    python probar_api.py http://127.0.0.1:8000

Solo usa la librería estándar de Python. Crea sus propios datos, comprueba los
códigos de estado de cada caso y al final borra lo que creó.
"""

import json
import sys
import urllib.error
import urllib.request

BASE = sys.argv[1].rstrip("/") if len(sys.argv) > 1 else "http://127.0.0.1:8000"

# Sin proxy: la API corre en este mismo equipo.
abridor = urllib.request.build_opener(urllib.request.ProxyHandler({}))

pruebas = 0
fallos = []
codigos_vistos = set()


def llamar(metodo: str, ruta: str, cuerpo: dict | None = None):
    """Envía una petición y devuelve (código de estado, cuerpo JSON o None)."""
    datos = json.dumps(cuerpo).encode("utf-8") if cuerpo is not None else None
    peticion = urllib.request.Request(
        BASE + ruta,
        data=datos,
        method=metodo,
        headers={"Content-Type": "application/json", "Accept": "application/json"},
    )
    try:
        with abridor.open(peticion, timeout=10) as respuesta:
            texto = respuesta.read().decode("utf-8")
            return respuesta.status, (json.loads(texto) if texto else None)
    except urllib.error.HTTPError as error:
        texto = error.read().decode("utf-8")
        try:
            return error.code, json.loads(texto)
        except ValueError:
            return error.code, texto
    except urllib.error.URLError:
        print(f"No fue posible conectarse con la API en {BASE}.")
        print("Verifica que esté encendida: fastapi dev app/main.py")
        sys.exit(2)


def comprobar(nombre: str, esperado: int, metodo: str, ruta: str, cuerpo=None):
    """Ejecuta una petición y verifica que el código sea el esperado."""
    global pruebas
    pruebas += 1
    codigo, datos = llamar(metodo, ruta, cuerpo)
    if codigo == esperado:
        codigos_vistos.add(codigo)
        print(f"  OK     {codigo}  {nombre}")
    else:
        fallos.append(nombre)
        print(f"  FALLA  {codigo} (esperado {esperado})  {nombre}")
        print(f"         respuesta: {datos}")
    return datos


def comprobar_condicion(nombre: str, condicion: bool):
    """Verifica una condición sobre los datos devueltos."""
    global pruebas
    pruebas += 1
    if condicion:
        print(f"  OK     --  {nombre}")
    else:
        fallos.append(nombre)
        print(f"  FALLA  --  {nombre}")


DUENO = {
    "nombre": "Ana Perez",
    "documento": "12345678",
    "telefono": "3001234567",
    "email": "ana@mail.com",
}
MASCOTA = {
    "nombre": "Firulais",
    "especie": "Perro",
    "raza": "Criollo",
    "edad": 3,
    "peso": 12.5,
}
CITA = {
    "fecha": "2026-11-27",
    "motivo": "Vacunacion anual",
    "veterinario": "Dr Lopez",
    "estado": "pendiente",
    "costo": 50000,
}

print(f"Probando la API en {BASE}\n")

print("DUEÑOS")
dueno = comprobar("crear dueño", 201, "POST", "/duenos", DUENO)
did = dueno["id"]
comprobar("dueño con nombre de 1 letra", 422, "POST", "/duenos", {**DUENO, "nombre": "A"})
comprobar("dueño con teléfono corto", 422, "POST", "/duenos", {**DUENO, "telefono": "123"})
comprobar("listar dueños", 200, "GET", "/duenos")
comprobar("consultar dueño", 200, "GET", f"/duenos/{did}")
comprobar("consultar dueño inexistente", 404, "GET", "/duenos/999999")
comprobar("reemplazar dueño", 200, "PUT", f"/duenos/{did}", {**DUENO, "nombre": "Ana Maria Perez"})
comprobar("reemplazar dueño inexistente", 404, "PUT", "/duenos/999999", DUENO)

print("\nMASCOTAS")
mascota = comprobar("crear mascota", 201, "POST", "/mascotas", {**MASCOTA, "dueno_id": did})
mid = mascota["id"]
comprobar("mascota con dueño inexistente", 404, "POST", "/mascotas", {**MASCOTA, "dueno_id": 999999})
comprobar("mascota con peso 0", 422, "POST", "/mascotas", {**MASCOTA, "dueno_id": did, "peso": 0})
comprobar("mascota con edad 41", 422, "POST", "/mascotas", {**MASCOTA, "dueno_id": did, "edad": 41})
comprobar("listar mascotas", 200, "GET", "/mascotas")
comprobar("consultar mascota", 200, "GET", f"/mascotas/{mid}")
comprobar("consultar mascota inexistente", 404, "GET", "/mascotas/999999")
comprobar("reemplazar mascota", 200, "PUT", f"/mascotas/{mid}", {**MASCOTA, "dueno_id": did, "edad": 4})
comprobar("reemplazar mascota con dueño inexistente", 404, "PUT", f"/mascotas/{mid}", {**MASCOTA, "dueno_id": 999999})

print("\nCITAS")
cita = comprobar("crear cita", 201, "POST", "/citas", {**CITA, "mascota_id": mid})
cid = cita["id"]
comprobar("cita con estado inválido", 422, "POST", "/citas", {**CITA, "mascota_id": mid, "estado": "hecha"})
comprobar("cita con costo negativo", 422, "POST", "/citas", {**CITA, "mascota_id": mid, "costo": -1})
comprobar("cita con mascota inexistente", 404, "POST", "/citas", {**CITA, "mascota_id": 999999})
comprobar("listar citas", 200, "GET", "/citas")
comprobar("consultar cita", 200, "GET", f"/citas/{cid}")
comprobar("consultar cita inexistente", 404, "GET", "/citas/999999")

print("\nENDPOINTS ADICIONALES")
lista = comprobar("relación: mascotas de un dueño", 200, "GET", f"/duenos/{did}/mascotas")
comprobar_condicion("  la lista incluye a la mascota creada", any(m["id"] == mid for m in lista))
comprobar("relación: dueño inexistente", 404, "GET", "/duenos/999999/mascotas")
pend = comprobar("filtro: citas pendientes", 200, "GET", "/citas?estado=pendiente")
comprobar_condicion(
    "  todas las devueltas están pendientes y está la nueva",
    all(c["estado"] == "pendiente" for c in pend) and any(c["id"] == cid for c in pend),
)
comprobar("reemplazar cita a atendida", 200, "PUT", f"/citas/{cid}", {**CITA, "mascota_id": mid, "estado": "atendida"})
aten = comprobar("filtro: citas atendidas", 200, "GET", "/citas?estado=atendida")
comprobar_condicion("  la cita aparece ahora como atendida", any(c["id"] == cid for c in aten))
pend = comprobar("filtro: pendientes otra vez", 200, "GET", "/citas?estado=pendiente")
comprobar_condicion("  la cita ya no aparece como pendiente", not any(c["id"] == cid for c in pend))
comprobar("filtro con estado inválido", 422, "GET", "/citas?estado=hecha")

print("\nELIMINAR (de hijo a padre)")
comprobar("eliminar dueño con mascotas", 409, "DELETE", f"/duenos/{did}")
comprobar("eliminar mascota con citas", 409, "DELETE", f"/mascotas/{mid}")
comprobar("eliminar cita", 204, "DELETE", f"/citas/{cid}")
comprobar("eliminar cita inexistente", 404, "DELETE", f"/citas/{cid}")
comprobar("eliminar mascota", 204, "DELETE", f"/mascotas/{mid}")
comprobar("eliminar dueño", 204, "DELETE", f"/duenos/{did}")
comprobar("consultar dueño eliminado", 404, "GET", f"/duenos/{did}")

print("\n" + "=" * 50)
requeridos = {200, 201, 204, 404, 409, 422}
faltan = sorted(requeridos - codigos_vistos)
print(f"Pruebas: {pruebas}   Fallos: {len(fallos)}")
print(f"Códigos exigidos por la rúbrica vistos: {sorted(codigos_vistos & requeridos)}")
if faltan:
    print(f"Códigos que no se probaron: {faltan}")
if fallos:
    print("\nFALLARON:")
    for nombre in fallos:
        print(f"  - {nombre}")
    sys.exit(1)
print("TODO CORRECTO")
