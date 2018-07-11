## Server call

### 1) Bathymetry
WCS give you real data
WCS: http://ows.emodnet-bathymetry.eu/wcs
The following formats are also usable, the python libraries to call and get the data is shown on the following websites. Here we mainly use wcs to get real data
/wcs (python): https://publicwiki.deltares.nl/display/OET/WCS+primer
/wfs: (access by r and python): https://publicwiki.deltares.nl/display/OET/WFS+primer
/wmts : you can find a similar link to exapalin how to access wmts
/wms: you can find a similar link to expalin how to access wmts

Below is a short exam illustrating how to use python for accessing wcs server to get real data (it is also on the website provided)

#### a WCS short documentation 
For finding specified params: https://publicwiki.deltares.nl/display/OET/WCS+primer

An example is shown here, for more concrete information, please check here:
```
http://geoport.whoi.edu/thredds/wcs/bathy/srtm30plus_v6?request=GetCoverage
&version=1.0.0
&service=WCS
&format=geotiff
&coverage=topo
&BBOX=0,50,10,55
```

##### Accessing WCS by python
This is an example from the website. It has been modified in our code to use the server of EMODnet (get capabilities): http://ows.emodnet-bathymetry.eu/wcs?service=WCS&request=GetCapabilities&version=1.0.0

We can (not neccesarily suggest) store the data in tif then we can check the previous project with R code to see how to load tif with raster
```
import os
import tempfile
// pip install owslib in the console
from owslib.wcs import WebCoverageService
// return a list of rasters
url = 'http://deltaresdata.openearth.nl/geoserver/ows?'
wcs = WebCoverageService(url,version='1.0.0')
wcs.identification.type
wcs.identification.title
list(wcs.contents)

// retrieve characterstics
sed = wcs['global:Sediment Thickness of the World']
sed.keywords
sed.grid.highlimits
sed.boundingboxes

// get a specific part for bounding box
// a tif will be written for a specified bounding box
requestbbox = (-6.283406057098745, 36.59251369598765, -6.266739197530853, 36.6050139853395)
requestwidth = 4
requestheight = 3
gc = wcs.getCoverage(identifier=sed.id,
                                  bbox=requestbbox,
                                  format='GeoTIFF',
                                  crs=sed.boundingboxes[0]['nativeSrs'],
                                  width=requestwidth,
                                  height=requestheight)
# random unique filename
tmpdir = tempfile.gettempdir()
fn = os.path.join(tmpdir,'test.tif')
f = open(fn,'wb')
f.write(gc.read())
gc.close()
f.close()
```
##### get data
In python, it should roughly work like this:
```
# Make data.
X = np.linspace(bbox[0],bbox[2], img.shape[1])
Y = np.linspace(bbox[1],bbox[3], img.shape[0])
X, Y = np.meshgrid(X, Y)
#R = np.sqrt(X**2 + Y**2)
#Z = np.sin(R)
Z = img
```


### 2) biotic observations

For this type of data, you can find service capabilities here: http://geo.vliz.be/geoserver/web/;jsessionid=72FFE2E1B91C15255DC7CA5D799ABC85?wicket:bookmarkablePage=:org.geoserver.web.demo.MapPreviewPage




### Direct access to data
Here is a website where you can directly download data to use (without waiting)
http://www.emodnet.eu/geonetwork/emodnet/eng/catalog.search#/home
The dataset is also sorted  by topics: Oceans, Geo information, boundaries, biota, economy, planning cadastre, environment, etc

Or you can go to their portal: http://portal.emodnet-bathymetry.eu/
Here you can directly download products/ area of interests which will give you .tif data (just select the data you want from left coner then select area of interest and download the data)

