"""
Validator functions for data validation across the application.
These validators can be used with Pydantic models or independently.
"""
import re
from datetime import datetime
from typing import Optional


def validate_date_string(value: Optional[str]) -> Optional[str]:
    """Validate that a date string has the correct format and represents a valid date.
    
    Args:
        value: The date string to validate, or None
        
    Returns:
        The validated date string, or None if input was None
        
    Raises:
        ValueError: If the date string has an invalid format or represents an invalid date
    """
    if value is None:
        return None
        
    # Check format
    date_pattern = re.compile(r'^\d{4}-\d{2}-\d{2}$')
    if not date_pattern.match(value):
        raise ValueError("Date must be in the format YYYY-MM-DD")
        
    # Check if it's a valid date
    try:
        datetime.strptime(value, '%Y-%m-%d')
    except ValueError:
        raise ValueError("Invalid date. Please provide a valid date in YYYY-MM-DD format")
    return value


def validate_date_range(start_date: Optional[str], end_date: Optional[str]) -> bool:
    """Validate that the end date is after the start date, if both are provided.
    
    Args:
        start_date: The start date string, or None
        end_date: The end date string, or None
        
    Returns:
        True if validation passes, otherwise raises ValueError
        
    Raises:
        ValueError: If end date is before or equal to start date
    """
    if not start_date or not end_date:
        return True
        
    # Convert string dates to date objects for comparison
    start = datetime.strptime(start_date, '%Y-%m-%d').date()
    end = datetime.strptime(end_date, '%Y-%m-%d').date()
    
    if end < start:
        raise ValueError("End date must be after start date")
        
    return True