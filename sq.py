import sys
import getopt

def main(x):
    x = int(x)
    print(x*x, file = sys.stdout)

if __name__ == "__main__":
    main(sys.argv[1])
