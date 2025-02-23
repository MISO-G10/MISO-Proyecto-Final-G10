from flask import Blueprint
from app.lib.database import db
from app.models.user import User

commands = Blueprint('db', __name__)


@commands.cli.command('create')
def create_db():
    print('Creating database...')
    db.create_all()

    print('Database created!')
    print("The following tables were created:")

    for table in db.metadata.tables:
        print(table)


@commands.cli.command('seed')
def seed_db():
    print('Seeding database...')

    for i in range(1, 6):
        user = User(
            name=f"User {i}",
            email="jhon@doe.com"
        )

        db.session.add(user)
        db.session.commit()

        print(user.to_dict())

    print('Database seeded!')
