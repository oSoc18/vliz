#setwd("~/Downloads/TEST_RSHINY")
#runApp("~/Downloads/TEST_RSHINY")
#install.packages("leaflet")
library(shiny)
library(leaflet)


r_colors <- rgb(t(col2rgb(colors()) / 255))
names(r_colors) <- colors()



# Define UI for miles per gallon application
shinyUI(pageWithSidebar(
  
  # Application title
  
  leafletOutput("mymap"),
  
  p(),
  actionButton("recalc", "New points")
  
))
