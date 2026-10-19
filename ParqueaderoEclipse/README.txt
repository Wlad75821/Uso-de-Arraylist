PROYECTO: Sistema de Parqueadero
Lenguaje: Java
Entorno recomendado: Eclipse IDE
Tipo: Aplicacion de consola

IMPORTAR EN ECLIPSE
1. Descomprime ParqueaderoEclipse.zip.
2. Abre Eclipse y selecciona File > Import.
3. Selecciona General > Existing Projects into Workspace.
4. En Select root directory, elige la carpeta ParqueaderoEclipse descomprimida.
5. Marca el proyecto y pulsa Finish.
6. Abre src/parqueadero/Main.java y ejecuta Run As > Java Application.

FUNCIONES
- Registrar ingreso de vehiculos.
- Consultar vehiculos estacionados.
- Registrar salida y calcular el cobro.
- Tarifa de $0.50 por cada hora o fraccion.
- Guardar vehiculos activos en data/vehiculos.txt.
- Registrar cobros e ingresos en data/caja.txt.

ARCHIVOS
Los archivos se crean automaticamente al ejecutar el programa. Se guardan dentro de la carpeta data del proyecto.
Si la aplicacion se ejecuta desde Eclipse, la ruta relativa depende del directorio de trabajo; se recomienda ejecutar el proyecto con la carpeta del proyecto como working directory.

FORMATO DE DATOS
vehiculos.txt: placa|marca|fechaHoraIngreso
caja.txt: placa|fechaHoraIngreso|fechaHoraSalida|horasCobradas|valor

NOTA
La tarifa es configurable mediante la constante TARIFA_POR_HORA en Parqueadero.java.
