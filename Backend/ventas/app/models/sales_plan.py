from sqlalchemy import Integer, String, Float, ForeignKey, Table, Column
from sqlalchemy.orm import mapped_column, Mapped, relationship

from app.lib.database import db
from .model import Model

sales_plan_sellers = Table(
    'sales_plan_sellers',
    db.metadata,
    Column('sales_plan_id', Integer, ForeignKey('sales_plans.id'), primary_key=True),
    Column('seller_id', Integer, ForeignKey('sellers.id'), primary_key=True)
)


class SalesPlan(db.Model, Model):
    """
    Sales Plan model representing sales targets for sellers.
    Has a many-to-many relationship with sellers.
    """
    __tablename__ = 'sales_plans'

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(String(500), nullable=False)
    target_amount: Mapped[float] = mapped_column(Float, nullable=False)
    start_date: Mapped[str] = mapped_column(String(10), nullable=False)  # YYYY-MM-DD format (10 chars)
    end_date: Mapped[str] = mapped_column(String(10), nullable=False)  # YYYY-MM-DD format (10 chars)

    # Many-to-many relationship with sellers
    sellers = relationship(
        "Seller",
        secondary=sales_plan_sellers,
        back_populates="sales_plans"
    )
