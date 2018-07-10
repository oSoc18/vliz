#install.packages("leaflet.extras")
#setwd("~/Downloads/TEST_RSHINY")
library(shiny)
library(leaflet.extras)


d<<-NULL

# Define server logic required to plot various variables against mpg
shinyServer(function(input, output) {
  
  
  output$mymap <- renderLeaflet({
    leaflet() %>%
      addProviderTiles(providers$Stamen.TonerLite, # providers$Stamen.Toner
                       options = providerTileOptions(noWrap = TRUE)
      ) %>% setView(lng = 11.5820, lat = 48.1351, zoom = 3)
      
  })
  
  output$cord_var <- renderText({ 
    cordinality <- list("North\t:\t","East\t:\t","South\t:\t", "West\t:\t")
    cords <- paste0(cordinality, input$mymap_bounds)
    paste(cords[1],cords[2],cords[3],cords[4], sep="\n")
    
  })
  observeEvent(input$WMS_button, {
    
    leafletProxy('mymap') %>%
      setView(lng = 2.122, lat = 51.428, zoom = 8) %>%
      
      addRectangles(
        lng1<-2.122, lat1<-51.428,
        lng2<-3.034, lat2<-51.548,
        fillColor = "transparent", layerId = "rectang"
      ) %>% 
      addWMSTiles(
        "http://213.122.160.75/scripts/mapserv.exe?map=D:/Websites/MeshAtlantic/map/MESHAtlantic.map&service=wms&version=1.1.0&request=GetMap&layers=EUSM2016_simplified200&srs=EPSG:4326&bbox=2.122,51.428,3.034,51.548&format=image/jpeg&styles=&width=450&height=60",
        layers = "EUSM2016_simplified200",
        options = WMSTileOptions(format = "image/png", transparent = TRUE)
        ) 
  })
  rv <- reactiveValues(first=NULL,second=NULL)
  curre <- NULL
  lstre <- NULL
  observeEvent(input$rect_button, {
    rv <- reactiveValues(first=NULL,second=NULL)
    print("reactive values")
    print(rv$first)
    print(rv$second)
    observeEvent(input$mymap_click,{
      click <-input$mymap_click
      if(is.null(click)){
        print("not a map click")
        return()
      }
      
      if(is.null(rv$first)){
        rv$first <- click
      }else{
        if(is.null(rv$second)){
          rv$second <- click
          
          output$clicked_var1<-renderText({
            #print(curre())
            paste0("Boundry 1 : \n","Lat : ",(rv$first)$lat,"\nLong : ",(rv$first)$lng)
          })
          output$clicked_var2<-renderText({
            paste0("Boundry 2 : \n","Lat : ",(rv$second)$lat,"\nLong : ",(rv$second)$lng)
          })
          
          leafletProxy('mymap') %>% 
            addRectangles(
              lng1<-(rv$first)$lng, lat1<-(rv$first)$lat,
              lng2<-(rv$second)$lng, lat2<-(rv$second)$lat,
              fillColor = "transparent", layerId = "rectang"
            )
        }
      }
        
      
    })
  })  
    
  observeEvent(input$clear_rect_button, {
    rv <- reactiveValues(first=NULL,second=NULL)
    rv$first <- NULL
    rv$second <- NULL
    
    
    leafletProxy('mymap') %>% clearShapes()
      
    output$clicked_var1<-renderText({
      NULL
    })
    output$clicked_var2<-renderText({
      NULL
    })
  })
  
  
  
  
})
