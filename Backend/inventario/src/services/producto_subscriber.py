from google.cloud import pubsub_v1
import json
from concurrent.futures import TimeoutError
from src.commands.create_producto import Create
from src.db.session import SessionLocal

class ProductoSubscriber:
    def __init__(self):
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path(
            'your-project-id', 'producto-registro-sub'
        )

    def callback(self, message):
        try:
            data = json.loads(message.data.decode('utf-8'))
            usuario_id = data['usuario_id']
            producto_data = data['producto']

            # Crear el producto usando el comando existente
            command = Create(usuario_id, producto_data)
            result = command.execute()

            # Si se creó exitosamente, confirmar el mensaje
            if 'id' in result:
                message.ack()
            else:
                # Si hubo error, no confirmar para que se reintente
                message.nack()

        except Exception as e:
            print(f"Error procesando mensaje: {str(e)}")
            message.nack()

    def start(self):
        streaming_pull_future = self.subscriber.subscribe(
            self.subscription_path,
            callback=self.callback
        )
        print(f"Iniciando servicio de procesamiento de productos...")

        try:
            streaming_pull_future.result()
        except TimeoutError:
            streaming_pull_future.cancel()
            streaming_pull_future.result()

        self.subscriber.close()
