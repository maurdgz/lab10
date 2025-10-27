# URLs del Proyecto Principal (lab08/urls.py)

from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    # Incluye TODAS las rutas de la aplicación 'series' en la raíz (path('')).
    # IMPORTANTE: Tu aplicación se llama 'series', no 'lab08'.
    path('', include('series.urls')), 
    
    # Rutas de administración
    path('admin/', admin.site.urls),
]