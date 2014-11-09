#!/bin/python

import xml.etree.ElementTree as ET
import sys

path = '{http://www.w3.org/2000/svg}path'
rect = '{http://www.w3.org/2000/svg}rect'

def main():
    tree = ET.parse(sys.argv[1])
    r=tree.getroot()
    s,start=getstart(r)
    end=getend(r,s)
    lines=getlines(r,s)
    p(start,end,lines)

def getstart(r):
    l=[e for e in r.iter(rect) if isstyred(e.get(key="style"))]
    if len(l) == 0:
        print("error: no start location")
        sys.exit(1)
    if len(l) > 1:
        print("warn: multiple start locations, taking first")
    e=l[0]
    x,y=float(e.get('x')),float(e.get('y'))
    s=1/float(e.get('width'))
    return (s,(x*s+1/2,(y*s+1/2)))

def getend(r,s):
    l=[e for e in r.iter(rect) if not isstyred(e.get(key="style"))]
    if len(l) == 0:
        print("error: no end location")
        sys.exit(1)
    if len(l) > 1:
        print("warn: multiple end locations, taking first")
    e=l[0]
    x1,y1=float(e.get('x')),float(e.get('y'))
    x2,y2=x1+float(e.get('width')),y1+float(e.get('height'))
    return (x1*s,y1*s,x2*s,y2*s)

def getlines(r,s):
    out=[]
    for e in r.iter(path):
        seq= [(float(x)*s,float(y)*s) for x,y in [pos for pos in
            [pos.split(',') for pos in e.get('d').split(' ')] if len(pos)==2]]
        phobic = isstyred(e.get(key='style'))
        cur=seq[0]
        seq=seq[1:]
        for p in seq:
            out.append((cur[0],cur[1],p[0],p[1],"true" if phobic else "false"))
            cur = p
    return out

def p(start,end,lines):
    print(start[0],start[1])
    print(end[0],end[1],end[2],end[3])
    print(len(lines))
    for l in lines:
        print(l[0],l[1],l[2],l[3],l[4])
    print(0)
 
def isstyred(sty):
    return sty.split('fill:')[1].split(';')[0] == '#ff0000'

def bname(tag):
    return tag.split('}')[1]

if __name__ == "__main__":
    main()
