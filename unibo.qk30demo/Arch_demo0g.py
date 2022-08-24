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
with Diagram('demo0gArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxtesta', graph_attr=nodeattr):
          componenta=Custom('componenta','./qakicons/symActorSmall.png')
     with Cluster('ctxtestb', graph_attr=nodeattr):
          componentb=Custom('componentb','./qakicons/symActorSmall.png')
     with Cluster('ctxtestc', graph_attr=nodeattr):
          componentc=Custom('componentc','./qakicons/symActorSmall.png')
     componenta >> Edge(color='magenta', style='solid', xlabel='atob') >> componentb
     componentb >> Edge(color='magenta', style='solid', xlabel='btoc') >> componentc
diag
