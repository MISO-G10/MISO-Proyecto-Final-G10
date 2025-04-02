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