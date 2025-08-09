# Chess MCP Bridge

Este proyecto es un puente (bridge) que facilita la integración entre aplicaciones de ajedrez y herramientas de asistencia basadas en Copilot. Proporciona una API REST que permite acceder a funcionalidades específicas relacionadas con el ajedrez.

## Características

- Obtención del usuario activo en el sistema
- Integración con Spring Boot para una API robusta y escalable

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Spring Boot 3.x

## Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/escontrela/chess-mcp-bridge.git
```

2. Navegar al directorio del proyecto:
```bash
cd chess-mcp-bridge
```

3. Compilar el proyecto:
```bash
./mvnw clean install
```

4. Ejecutar la aplicación:
```bash
./mvnw spring-boot:run
```

## Uso

La aplicación expone los siguientes endpoints:

- `GET /test/getActiveUser`: Obtiene información sobre el usuario activo en el sistema

## Contribuir

Las contribuciones son bienvenidas. Por favor, siéntete libre de crear un Pull Request o abrir un Issue.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT.
