from . import api

@api.route('/health', methods=['GET'])
def health():
    return {'status': 'ok'}
