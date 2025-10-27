# URLs de la Aplicación (series/urls.py)

from django.urls import path
from . import views

urlpatterns = [
    # 1. Raíz/Index 
    path('', views.IndexView.as_view(), name='index'), 

    # 2. Listado de Series (Coincide con la clase SerieList en views.py)
    path('serie/', views.SerieList.as_view(), name='series'), 

    # 3. Detalle, Edición y Eliminación (Coincide con la clase SerieDetail en views.py)
    # IMPORTANTE: Usamos 'pk' en el URL para que coincida con el argumento en views.py
    path('serie/<int:pk>', views.SerieDetail.as_view(), name='serie_id'),
]