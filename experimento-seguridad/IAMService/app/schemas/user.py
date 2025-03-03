from app.lib.schema import marshmallow
from app.models.user import User

class UserSchema(marshmallow.SQLAlchemyAutoSchema):
    class Meta:
        model = User
