from flask import Blueprint
from app.lib.database import db
from app.models.sales_plan_seller import SalesPlanSeller
from app.models.sales_plan import SalesPlan
from datetime import datetime, timedelta

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
    """
    Seed the database with initial data for development and testing.
    Creates sellers, sales plans, and establishes relationships between them.
    """
    print('Seeding database...')

    # Ensure tables exist
    print('Ensuring database tables exist...')
    db.create_all()

    # Clear existing data
    print('Clearing existing data...')
    try:
        # Use raw SQL to avoid issues with foreign keys
        db.session.execute(db.text("DELETE FROM sales_plan_sellers"))
        db.session.execute(db.text("DELETE FROM sales_plans"))
        db.session.commit()
    except Exception as e:
        print(f"Warning: Failed to clear existing data: {e}")
        db.session.rollback()

    # Define sellers that we'll add to sales plans later
    print('Preparing seller data...')
    seller_names = [
        "Carlos Rodríguez",
        "Ana Martínez",
        "Javier López",
        "María González",
        "Daniel Pérez"
    ]

    # We'll use this seller data when creating sales plans

    # Create sales plans
    print('Creating sales plans...')
    today = datetime.now().date()

    # Plan 1: Current quarter plan
    current_quarter_start = today.replace(day=1)
    current_month = today.month
    if current_month <= 3:
        current_quarter_start = current_quarter_start.replace(month=1)
    elif current_month <= 6:
        current_quarter_start = current_quarter_start.replace(month=4)
    elif current_month <= 9:
        current_quarter_start = current_quarter_start.replace(month=7)
    else:
        current_quarter_start = current_quarter_start.replace(month=10)

    current_quarter_end = (current_quarter_start.replace(month=current_quarter_start.month + 2) +
                           timedelta(days=31)).replace(day=1) - timedelta(days=1)

    plan1 = SalesPlan(
        nombre="Q1 2025 Sales Campaign",
        descripcion="First quarter sales targets focusing on new client acquisition",
        valor_objetivo=50000.0,
        fecha_inicio=current_quarter_start.strftime('%Y-%m-%d'),
        fecha_fin=current_quarter_end.strftime('%Y-%m-%d')
    )

    # Add the sales plan first without sellers
    db.session.add(plan1)
    db.session.flush()  # Flush to get the ID

    # Add 3 sellers to this plan
    for i, name in enumerate(seller_names[:3], start=100):
        seller = SalesPlanSeller(
            nombre=name,
            seller_id=i,  # External ID from users microservice
            sales_plan_id=plan1.id
        )
        db.session.add(seller)
        print(f"Added seller: {name} (ID: {i}) to plan: {plan1.nombre}")

    # Plan 2: Next quarter plan
    next_quarter_start = (current_quarter_end + timedelta(days=1))
    next_quarter_end = (next_quarter_start + timedelta(days=90))

    plan2 = SalesPlan(
        nombre="Q2 2025 Expansion Plan",
        descripcion="Second quarter targets focusing on expanding existing accounts",
        valor_objetivo=75000.0,
        fecha_inicio=next_quarter_start.strftime('%Y-%m-%d'),
        fecha_fin=next_quarter_end.strftime('%Y-%m-%d')
    )

    # Add the sales plan first without sellers
    db.session.add(plan2)
    db.session.flush()  # Flush to get the ID

    # Add all sellers to this plan
    for i, name in enumerate(seller_names, start=200):
        seller = SalesPlanSeller(
            nombre=name,
            seller_id=i,  # External ID from users microservice
            sales_plan_id=plan2.id
        )
        db.session.add(seller)
        print(f"Added seller: {name} (ID: {i}) to plan: {plan2.nombre}")

    # Plan 3: Annual plan
    year_start = today.replace(month=1, day=1)
    year_end = today.replace(month=12, day=31)

    plan3 = SalesPlan(
        nombre="2025 Annual Targets",
        descripcion="Overall annual sales targets for all territories",
        valor_objetivo=250000.0,
        fecha_inicio=year_start.strftime('%Y-%m-%d'),
        fecha_fin=year_end.strftime('%Y-%m-%d')
    )

    # Add the sales plan first without sellers
    db.session.add(plan3)
    db.session.flush()  # Flush to get the ID

    # Add 2 different sellers to this plan
    for i, name in enumerate(seller_names[3:], start=300):
        seller = SalesPlanSeller(
            nombre=name,
            seller_id=i,  # External ID from users microservice
            sales_plan_id=plan3.id
        )
        db.session.add(seller)
        print(f"Added seller: {name} (ID: {i}) to plan: {plan3.nombre}")

    # Commit all changes
    db.session.commit()

    # Print summary
    print('\nDatabase seeded successfully!')
    print(f"Created 3 sales plans with associated sellers:")
    print(f"  - Plan 1: 3 sellers")
    print(f"  - Plan 2: 5 sellers")
    print(f"  - Plan 3: 2 sellers")
    print("\nUse the API endpoints to interact with this data.")
