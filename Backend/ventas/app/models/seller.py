from sqlalchemy import Integer, String
from sqlalchemy.orm import mapped_column, Mapped, relationship

from app.lib.database import db
from .model import Model


class Seller(db.Model, Model):
    """
    Seller model representing a minimal reference to users in the users microservice.
    This avoids duplicating all user data while allowing us to maintain references.
    """
    __tablename__ = 'sellers'

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    seller_id: Mapped[int] = mapped_column(Integer, unique=True, nullable=False)

    # Relationship with sales plans
    sales_plans = relationship(
        "SalesPlan",
        secondary="sales_plan_sellers",
        back_populates="sellers"
    )
