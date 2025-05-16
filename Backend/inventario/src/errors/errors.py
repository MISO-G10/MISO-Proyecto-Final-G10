class ApiError(Exception):
    code = 422
    description = "Mensaje genérico"

    def __init__(self, description=None, code=None):
        if description:
            self.description = description

        if code:
            self.code = code

    def to_dict(self):
        return {
            "status": self.code,
            "msg": self.description
        }


class Unauthorized(ApiError):
    code = 401
    description = "Acceso no autorizado"


class Forbidden(ApiError):
    code = 403
    description = "Acceso no autorizado"


class Forbidden(ApiError):
    code = 403
    description = "Acceso no autorizado"


class InvalidProductoData(ApiError):
    code = 400
    description = "Datos del producto no válidos"


class InvalidBodegaData(ApiError):
    code = 400
    description = "Datos de la bodega no válidos"


class ResourceNotFound(ApiError):
    code = 404
    description = "Recurso no encontrado"

class InvalidPedidoData(ApiError):
    code = 400
    description = "Datos del pedido no válidos"

class InvalidStockPedido(ApiError):
    code = 400

    def __init__(self, producto_id, inventario_total, cantidad_solicitada):
        description = (
            f"No hay suficiente stock para el producto con ID {producto_id}. "
            f"Disponible: {inventario_total}, Solicitado: {cantidad_solicitada}"
        )
        super().__init__(description=description, code=self.code)