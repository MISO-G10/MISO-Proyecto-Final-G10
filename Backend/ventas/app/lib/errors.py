class ApiError(Exception):
    def __init__(self, message, status_code):
        super().__init__(message)

        self.message = message
        self.status_code = status_code

    def get_message(self):
        return self.message

    def get_status_code(self):
        return self.status_code

    def __str__(self):
        return f'{self.message} ({self.status_code})'

    def to_dict(self):
        return {
            'message': self.message,
        }


class NotFoundError(ApiError):
    def __init__(self, message="Resource not found"):
        super().__init__(message, 404)


class BadRequestError(ApiError):
    def __init__(self, message="Bad request"):
        super().__init__(message, 400)


class ConflictError(ApiError):
    def __init__(self, message="Resource conflict"):
        super().__init__(message, 409)


class UnauthorizedError(ApiError):
    def __init__(self, message="Unauthorized"):
        super().__init__(message, 401)


class ForbiddenError(ApiError):
    def __init__(self, message="Forbidden"):
        super().__init__(message, 403)