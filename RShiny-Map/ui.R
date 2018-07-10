#setwd("~/Downloads/TEST_RSHINY")
#runApp("~/Downloads/TEST_RSHINY")
#install.packages("leaflet")
library(shiny)
library(leaflet)


r_colors <- rgb(t(col2rgb(colors()) / 255))
names(r_colors) <- colors()



# Define UI for miles per gallon application

shinyUI(fluidPage(
  
  titlePanel("Open Sea Lab"),
  
  navbarPage("EMODnet data explorer",
    tabPanel("Select your Area of Interest",
      sidebarLayout(
               
        sidebarPanel(
          actionButton("rect_button", "Draw a rectangle"),
          actionButton("clear_rect_button", "Clear rectangle"),
          p(), # creates a space
          verbatimTextOutput("clicked_var1"),
          verbatimTextOutput("clicked_var2"),
          p(), # creates a space
          verbatimTextOutput("cord_var"),
          p(),
          actionButton("WMS_button", "Test WMS Button")
        ),
        mainPanel(
          leafletOutput("mymap")
        )
      )             
    ),
    tabPanel("Select Data Layers"
      
    ),
    tabPanel("Review and Confirm"
             
    )
  )
  
))
