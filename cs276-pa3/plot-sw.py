import numpy as np
import matplotlib.pyplot as plt

def load_data(f, norm=0):
    with open(f) as data_file:
        pairs = [map(float, line.strip().split()) for line in data_file]

    x = [p[0] for p in pairs]
    y = [p[1] - norm for p in pairs]

    return x, y

if __name__ == '__main__':
    import sys
    if len(sys.argv) < 4:
        print "Usage: python %s <train_output> <dev_output> <output_pdf>" % sys.argv[0]
        sys.exit(1)

    x1, y1 = load_data(sys.argv[1], 0.8651)
    x2, y2 = load_data(sys.argv[2], 0.8501)

    plt.plot(x1, y1, 'r-', x2, y2, 'b-', linewidth=1)
    plt.legend(("Train", "Dev"), loc=3)
    plt.xlabel("B")
    plt.ylabel("diff")
    plt.savefig(sys.argv[3])
