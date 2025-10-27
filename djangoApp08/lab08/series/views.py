from rest_framework.views import APIView # <-- CLASE BASE CORRECTA
from rest_framework.response import Response
# from django.views import View  # <-- ELIMINAR: No la necesitas si usas APIView
from .models import Serie
from .serializers import SerieSerializer

class IndexView(APIView):
    # La clase IndexView hereda correctamente de APIView.
    def get(self, request):
        context = {'mensaje': 'servidor activo'}
        return Response(context)

class SerieList(APIView): # <-- CORREGIDO: HEREDA DE APIView
    # La clase de listado/creación (GET, POST)
    def get(self, request):
        dataSeries = Serie.objects.all()
        serSeries = SerieSerializer(dataSeries, many=True)
        return Response(serSeries.data)
    
    def post(self, request):
        serSerie = SerieSerializer(data=request.data)
        serSerie.is_valid(raise_exception=True)
        serSerie.save()
        return Response(serSerie.data)
    
class SerieDetail(APIView): # <-- CORREGIDO: HEREDA DE APIView
    # La clase de detalle/edición/eliminación (GET, PUT, DELETE)
    
    # IMPORTANTE: En APIView, los métodos reciben la Primary Key (pk) como argumento
    def get(self, request, pk): # <-- CAMBIADO de serie_id a pk
        dataSerie = Serie.objects.get(pk=pk) # <-- USAMOS pk
        serSerie = SerieSerializer(dataSerie)
        return Response(serSerie.data)
    
    def put(self, request, pk): # <-- CAMBIADO de serie_id a pk
        dataSerie = Serie.objects.get(pk=pk) # <-- USAMOS pk
        serSerie = SerieSerializer(dataSerie, data=request.data)
        serSerie.is_valid(raise_exception=True)
        serSerie.save()
        return Response(serSerie.data)
    
    def delete(self, request, pk): # <-- CAMBIADO de serie_id a pk
        dataSerie = Serie.objects.get(pk=pk) # <-- USAMOS pk
        serSerie = SerieSerializer(dataSerie)
        dataSerie.delete()
        return Response(serSerie.data)