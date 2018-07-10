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
  
  rv <- reactiveValues(lstval=NULL,curval=NULL)
  curre <- NULL
  lstre <- NULL
  observeEvent(input$rect_button, {
    observeEvent(input$mymap_click,{
      click<-input$mymap_click
      
      if(is.null(rv$lstval) || is.null(rv$curval) ){
        
        rv$lstval <- rv$curval
        rv$curval <- click
        curre <- reactive({req(click);  click; rv$curval})
        lstre <- reactive({req(click);  click; rv$lstval})
        print("vars ---")
        print(paste0("Boundry 1 : \n","Lat : ",curre()$lat,"\nLong : ",curre()$lng)) 
        print(paste0("Boundry 2 : \n","Lat : ",lstre()$lat,"\nLong : ",lstre()$lng))
        output$clicked_var<-renderText({
          #print(curre())
          paste0("Boundry 1 : \n","Lat : ",curre()$lat,"\nLong : ",curre()$lng)
        })
        output$clicked_var1<-renderText({
          paste0("Boundry 2 : \n","Lat : ",lstre()$lat,"\nLong : ",lstre()$lng)
        })
        
        
        
      }
      if(!is.null(rv$lstval) && !is.null(rv$curval)){
        
        print("trying to print rectangle")
        print(rv$lstval) 
        print(rv$curval)
        leafletProxy('mymap') %>% 
            addRectangles(
              lng1<-(rv$lstval)$lng, lat1<-(rv$lstval)$lat,
              lng2<-(rv$curval)$lng, lat2<-(rv$curval)$lat,
              fillColor = "transparent"
            )
       
      }
      
    
    })
  })  
  observeEvent(input$clear_rect_button, {
    rv$curval=NULL
    rv$lstval=NULL
    output$clicked_var<-renderText({
      NULL
    })
    output$clicked_var1<-renderText({
      NULL
    })
  })
  
  observeEvent(input$rect_button, {
    print(lstre)
    
  })
  
  
  
})
