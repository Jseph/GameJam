#!/bin/python
import sys
from math import *

# radius, dtheta, thetamin, thetamax
def main():
    if len(sys.argv)!=5:
        usage()
    r=float(sys.argv[1])
    dtheta=float(sys.argv[2])
    thetamin=float(sys.argv[3])
    thetamax=float(sys.argv[4])
    ss=surfaces(r,dtheta,thetamin,thetamax)
    p(ss)

def surfaces(r,dtheta,thetamin,thetamax):
    surfaces=[]

    x2=r*cos(thetamin)
    y2=r*sin(thetamin)
    x1=x2+y2*2
    y1=y2-x2*2
    surfaces.append((x1,y1,x2,y2))

    theta=thetamin
    while (1):
        if theta > thetamax:
            break
        x1=r*cos(theta)
        y1=r*sin(theta)
        x2=r*cos(theta+dtheta)
        y2=r*sin(theta+dtheta)

        surfaces.append((x1,y1,x2,y2))
        theta+=dtheta

    x1=r*cos(thetamax)
    y1=r*sin(thetamax)
    x2=x1-y1*2
    y2=y1+x1*2
    surfaces.append((x1,y1,x2,y2))

    return surfaces


def p(ss):
    print(0,0)
    print(1000,1000,1000,1000)
    print(len(ss))
    for s in ss:
        print(s[0],-s[1],s[2],-s[3],"true")
    print(0)

def usage():
    print("usage: " + sys.argv[0] + " <r> <dtheta> <thetamin> <thetamax>\n" 
        + "\tsuch that thetamin < thetamax")
    sys.exit(1)

if __name__ == "__main__":
    main()
