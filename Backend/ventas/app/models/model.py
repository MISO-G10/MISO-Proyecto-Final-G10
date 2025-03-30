from datetime import datetime, timezone

from sqlalchemy import DateTime
from sqlalchemy.orm import mapped_column, Mapped

from sqlalchemy_mixins.serialize import SerializeMixin


class Model(SerializeMixin):
    createdAt: Mapped[datetime] = mapped_column(DateTime, default=datetime.now(timezone.utc))
    updatedAt: Mapped[datetime] = mapped_column(DateTime, default=datetime.now(timezone.utc),
                                                onupdate=datetime.now(timezone.utc))
