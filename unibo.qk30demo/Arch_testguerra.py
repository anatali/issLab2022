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
with Diagram('testguerraArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctx_wasteservice_proto_ctx', graph_attr=nodeattr):
          wastetruck=Custom('wastetruck','./qakicons/symActorSmall.png')
          sonarshim=Custom('sonarshim','./qakicons/symActorSmall.png')
          pathexec=Custom('pathexec','./qakicons/symActorSmall.png')
     wastetruck >> Edge(color='magenta', style='solid', xlabel='dopath') >> pathexec
     sonarshim >> Edge(color='blue', style='solid', xlabel='stopPath') >> pathexec
     sonarshim >> Edge(color='blue', style='solid', xlabel='resumePath') >> pathexec
     pathexec >> Edge( xlabel='demo', **eventedgeattr) >> sys
     pathexec >> Edge(color='green', style='dashed', xlabel='dopathdone') >> sys 
diag
