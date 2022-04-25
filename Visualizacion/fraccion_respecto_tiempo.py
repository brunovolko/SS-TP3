import pandas
from matplotlib import pyplot as plt
import numpy as np

#Lee evolucion_distribucion.csv y genera gráfico

df = pandas.read_csv('evolucion_distribucion.csv')
min_value = df['fraccion'].min()
plt.plot(df['t'], df['fraccion'])
plt.scatter(df.where(df['fraccion'] <= 0.5).dropna().iloc[0]['t'], df.where(df['fraccion'] <= 0.5).dropna().iloc[0]['fraccion'], marker='o', s=50, zorder=2)
plt.xlabel('Tiempo (s)')
plt.ylabel('Fraccion de particulas a izquierda')
plt.yticks(np.append(np.arange(min_value, 1, 0.03), [1]))
plt.grid()
plt.legend(loc='best')
plt.savefig('distribucion_respecto_tiempo.png')
