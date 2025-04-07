from abc import ABC, abstractmethod


class BaseCommand(ABC):
    """
    Base command class for implementing the command pattern.
    Each command should inherit from this class and implement the execute method.
    """
    
    @abstractmethod
    def execute(self):
        """
        Execute the command. This method must be implemented by all commands.
        
        Returns:
            The result of the command execution.
        """
        pass