import pandas
from matplotlib import pyplot as plt

df = pandas.read_csv('evolucion_distribucion.csv')

plt.plot(df['t'], df['promedio'])
plt.errorbar(df['t'], df['promedio'], df['desvio'], linestyle='None', marker='')
plt.xlabel('Tiempo (s)')
plt.ylabel('Fraccion de particulas a izquierda')

plt.savefig('distribucion_respecto_tiempo.png')
