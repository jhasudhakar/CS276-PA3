#!/usr/bin/python
from subprocess import Popen
from subprocess import PIPE
import numpy as np

CONFIG = 'bm25.config'

bm25config = {
    'Wf#body': 1.0,
    'lambdaPrime': 1.0,
    'Wf#anchor': 1.0,
    'Bf#header': 0.5,
    'Wf#title': 1.0,
    'Bf#anchor': 0.5,
    'K1': 1.0,
    'Wf#url': 1.0,
    'Bf#body': 0.5,
    'Bf#url': 0.5,
    'lambda': 1.5,
    'Wf#header': 1.0,
    'Bf#title': 0.5
}

commands = ["./run.sh", "train", "bm25"]

def func(x, *params):
    bm25config['Wf#url'] = x[0]
    bm25config['Wf#title'] = x[1]
    bm25config['Wf#body'] = x[2]
    bm25config['Wf#header'] = x[3]
    bm25config['Wf#anchor'] = x[4]
    # bm25config['Bf#url'] = x[5]
    # bm25config['Bf#title'] = x[6]
    # bm25config['Bf#body'] = x[7]
    # bm25config['Bf#header'] = x[8]
    # bm25config['Bf#anchor'] = x[9]
    # bm25config['K1'] = x[10]
    # bm25config['lambda'] = x[11]
    # bm25config['lambdaPrime'] = x[12]
    write_dict_to_file(bm25config, CONFIG)
    p = Popen(commands, stdout=PIPE)
    output = float(p.communicate()[0].strip())
    print "config:", bm25config
    print "ndcg:", output
    print
    return -output

def write_dict_to_file(dictionary, filename):
    """Write a dictionary to file
    """
    with open(filename, 'w') as f:
        for key in dictionary:
            f.write(key + '=' + str(dictionary[key]) + '\n')

if __name__ == '__main__':
    write_dict_to_file(bm25config, 'bm25.config')
    # x0 = np.array([1, 1, 1, 1, 1, 0.5, 0.5, 0.5, 0.5, 0.5, 1, 1.5, 1])
    # from scipy.optimize import anneal
    # res = anneal(func, x0, schedule='boltzmann', full_output=True, maxiter=100, lower=lower, upper=upper, disp=True)
    from scipy.optimize import brute, fmin
    rranges = (slice(0, 2, 0.2), slice(0, 2, 0.2), slice(0, 2, 0.2), slice(0, 2, 0.2), slice(0, 2, 0.2))
    res = brute(func, rranges, full_output=True, finish=fmin)
    print res[0]
    print res[1]
