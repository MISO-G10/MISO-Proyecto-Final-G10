from flask import Flask, request, jsonify
from flask_jwt_extended import jwt_required
from app.models.user import User
from . import api

        
'''Método para que un usuario haga login en la aplicación, requiere una combinación usuario y 
contraseña que exista en la base de datos para generar un token de acceso'''
#@api.route('/login', methods=['POST'])
    

'''Método para verificar que el token de acceso exista en la consulta y sea de un usuario válido
si es válido retorna las reglas de acceso de dicho usuario'''
#@api.route('/validar-token', methods=['GET'])
    
    




