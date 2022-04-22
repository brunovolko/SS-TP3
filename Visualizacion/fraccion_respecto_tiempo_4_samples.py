import pandas
from matplotlib import pyplot as plt
import numpy as np

df_100_02 = pandas.read_csv('evolucion_distribucion1.csv')
df_300_02 = pandas.read_csv('evolucion_distribucion2.csv')
df_100_045 = pandas.read_csv('evolucion_distribucion3.csv')
df_300_045 = pandas.read_csv('evolucion_distribucion4.csv')
min_value = min([df_100_02['fraccion'].min(), df_300_02['fraccion'].min(), df_100_045['fraccion'].min()])
plt.plot(df_100_02['t'], df_100_02['fraccion'], label='N=100, D=0.02', zorder=1, color='blue')
plt.plot(df_300_02['t'], df_300_02['fraccion'], label='N=300, D=0.02', zorder=1, color='orange')
plt.plot(df_100_045['t'], df_100_045['fraccion'], label='N=100, D=0.045', zorder=1, color='green')
plt.plot(df_300_045['t'], df_300_045['fraccion'], label='N=300, D=0.045', zorder=1, color='red')
plt.scatter(df_100_02.where(df_100_02['fraccion'] <= 0.5).dropna().iloc[0]['t'], df_100_02.where(df_100_02['fraccion'] <= 0.5).dropna().iloc[0]['fraccion'], color='#111163', marker='o', s=50, zorder=2)
plt.scatter(df_300_02.where(df_300_02['fraccion'] <= 0.5).dropna().iloc[0]['t'], df_300_02.where(df_300_02['fraccion'] <= 0.5).dropna().iloc[0]['fraccion'], color='#9a6b15', marker='o', s=50, zorder=2)
plt.scatter(df_100_045.where(df_100_045['fraccion'] <= 0.5).dropna().iloc[0]['t'], df_100_045.where(df_100_045['fraccion'] <= 0.5).dropna().iloc[0]['fraccion'], color='#0b450b', marker='o', s=50, zorder=2)
plt.scatter(df_300_045.where(df_300_045['fraccion'] <= 0.5).dropna().iloc[0]['t'], df_300_045.where(df_300_045['fraccion'] <= 0.5).dropna().iloc[0]['fraccion'], color='#890f0f', marker='o', s=50, zorder=2)
plt.xlabel('Tiempo (s)')
plt.ylabel('Fraccion de particulas a izquierda')
plt.yticks(np.append(np.arange(min_value, 1, 0.03), [1]))
plt.grid()
plt.legend(loc='best')
plt.savefig('distribucion_respecto_tiempo.png')
