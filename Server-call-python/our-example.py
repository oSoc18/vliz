
# import packages
import os
# pip install owslib
# for gdal install please check https://github.com/OSGeo/gdal
from owslib.wcs import WebCoverageService
from osgeo import gdal
  
# you can try this part without import gdal 
# theImage and requestbox will mainly work for getting the data

# define the connection
url = 'http://ows.emodnet-bathymetry.eu/wcs?'
wcs = WebCoverageService(url,version='1.0.0',timeout=320)
wcs.identification.type
wcs.identification.title
  
# define variables
clipfile = r'..\temp.tif'
requestbbox = (2.097,52.715,4.277,53.935)
# for other capabilities besides mean_atlas_land
# WCS: http://ows.emodnet-bathymetry.eu/wcs?service=WCS&request=GetCapabilities&version=1.0.0
# mean multicolor, mean rainbow color
layer = 'emodnet:mean_atlas_land'


# get the data
bathyml = 'emodnet:mean'
sed = wcs[layer] #this is necessary to get essential metadata from the layers advertised
sed.keywords
sed.grid.highlimits
sed.boundingboxes
cx, cy = map(int,sed.grid.highlimits)
bbox = sed.boundingboxes[0]['bbox']
lx,ly,hx,hy = map(float,bbox)
resx, resy = (hx-lx)/cx, (hy-ly)/cy
width = cx/1000
height = cy/1000
 
gc = wcs.getCoverage(identifier=bathyml,
         bbox=requestbbox,
         coverage=sed,
         format='GeoTIFF',
         crs=sed.boundingboxes[0]['nativeSrs'],
         width=width,
         height=height)
 
fn = clipfile
f = open(fn,'wb')
f.write(gc.read())
f.close()
# here you need the gdal installed
filetiff = gdal.Open(clipfile)
theImage = filetiff.GetRasterBand(1).ReadAsArray()
os.unlink(clipfile)
























