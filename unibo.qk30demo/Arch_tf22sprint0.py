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
with Diagram('tf22sprint0Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxwasteservice', graph_attr=nodeattr):
          wasteservice=Custom('wasteservice','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          wastewervicewtatusgui=Custom('wastewervicewtatusgui','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxdriver', graph_attr=nodeattr):
          driversimulator=Custom('driversimulator','./qakicons/symActorSmall.png')
     with Cluster('ctxrasp', graph_attr=nodeattr):
          sonaronrasp=Custom('sonaronrasp','./qakicons/symActorSmall.png')
     wasteservice >> Edge(color='blue', style='solid', xlabel='guiupdate') >> wastewervicewtatusgui
     wasteservice >> Edge(color='magenta', style='solid', xlabel='todocmd') >> transporttrolley
     transporttrolley >> Edge(color='blue', style='solid', xlabel='guiupdate') >> wastewervicewtatusgui
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> transporttrolley
     transporttrolley >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='step') >> basicrobot
     sys >> Edge(color='red', style='dashed', xlabel='resume') >> transporttrolley
     driversimulator >> Edge(color='magenta', style='solid', xlabel='depositrequest') >> wasteservice
     sonaronrasp >> Edge( xlabel='alarm', **eventedgeattr) >> sys
     sonaronrasp >> Edge( xlabel='resume', **eventedgeattr) >> sys
diag
