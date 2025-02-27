from flask import jsonify

from . import api


@api.route('/hello-world', methods=['GET'])
def hello():
    return jsonify({'message': 'monitor says hello!'})
