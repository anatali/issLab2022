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
with Diagram('tf22Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxdriver', graph_attr=nodeattr):
          driver=Custom('driver','./qakicons/symActorSmall.png')
     with Cluster('ctxwasteservice', graph_attr=nodeattr):
          wasteservice=Custom('wasteservice','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxrasp', graph_attr=nodeattr):
          sonaronrasp=Custom('sonaronrasp','./qakicons/symActorSmall.png')
     wasteservice >> Edge(color='magenta', style='solid', xlabel='pickup') >> transporttrolley
     wasteservice >> Edge(color='green', style='dashed', xlabel='loadrejected') >> sys 
     wasteservice >> Edge(color='green', style='dashed', xlabel='loadaccept') >> sys 
     wasteservice >> Edge( xlabel='pickupdone', **eventedgeattr) >> sys
     wasteservice >> Edge(color='magenta', style='solid', xlabel='gotobox') >> transporttrolley
     wasteservice >> Edge(color='green', style='dashed', xlabel='loadrejected') >> sys 
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='step') >> basicrobot
     transporttrolley >> Edge(color='green', style='dashed', xlabel='pickupanswer') >> sys 
     transporttrolley >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     driver >> Edge(color='magenta', style='solid', xlabel='depositrequest') >> wasteservice
     sys >> Edge(color='red', style='dashed', xlabel='pickupdone') >> driver
     sonaronrasp >> Edge( xlabel='alarm', **eventedgeattr) >> sys
     sonaronrasp >> Edge( xlabel='resume', **eventedgeattr) >> sys
diag
