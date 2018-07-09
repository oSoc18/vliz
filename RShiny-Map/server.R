library(shiny)
#setwd("~/Downloads/TEST_RSHINY")

# Define server logic required to plot various variables against mpg
shinyServer(function(input, output) {
  
  
  points <- eventReactive(input$recalc, {
    cbind(rnorm(40) * 2 + 13, rnorm(40) + 48)
  }, ignoreNULL = FALSE)
  
  output$mymap <- renderLeaflet({
    leaflet() %>%
      addProviderTiles(providers$Stamen.Toner, # providers$Stamen.Toner
                       options = providerTileOptions(noWrap = TRUE)
      ) %>%
      addMarkers(data = points())
  })
  
  
})
