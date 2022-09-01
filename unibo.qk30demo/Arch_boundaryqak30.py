from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('boundaryqak30Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxboundaryqak30', graph_attr=nodeattr):
          boundaryqak30=Custom('boundaryqak30','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxconsole', graph_attr=nodeattr):
          cmdconsole=Custom('cmdconsole','./qakicons/symActorSmall.png')
     with Cluster('ctxobserver', graph_attr=nodeattr):
          applobserver=Custom('applobserver','./qakicons/symActorSmall.png')
     boundaryqak30 >> Edge(color='green', style='dashed', xlabel='oktogo') >> sys 
     boundaryqak30 >> Edge(color='magenta', style='solid', xlabel='step') >> basicrobot
     boundaryqak30 >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     cmdconsole >> Edge(color='magenta', style='solid', xlabel='ready') >> boundaryqak30
     cmdconsole >> Edge( xlabel='alarm', **eventedgeattr) >> sys
     boundaryqak30 >> Edge(color='blue', style='solid', xlabel='coapUpdate') >> applobserver
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> applobserver
diag
