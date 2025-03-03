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
    def __init__(self, message):
        super().__init__(message, 404)
