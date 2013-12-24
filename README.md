# GridRef

Convert an alpha numeric Ordnance Survey grid reference to easting and northing or vice versa.

Valid input includes:

    ST           => The bottom right coordinate of the ST grid square
    NN1665071250 => The easting and northing of Ben Nevis
    SU387148     => The Ordnance Survey offices in Southampton
    TQ336805     => The Tower of London

    "533600.0 180500.0" => TQ3360080500 (The Tower of London)

## Build

    $ lein uberjar

## Usage

Using the lein:

    $ lein run [args]

Using the standalone jar:

    $ java -jar gridref-0.1.0-standalone.jar [args]

## Examples

Determine the coordinates in British National Grid of the Ordnance Survey Offices in Southampton, UK

    $ java -jar gridref-0.1.0-standalone.jar SU387148

Will output:

    [438700.0 114800.0]

## Todo

* Update readme with usage and examples (or link to it)
* Exit with 1 on error
* Shell script to alias as gridref
* Output in WTK or GeoJSON?
* Handle NE, SW etc. suffixes

## Possible cli interface

    gridref --digits 4 "123456 123456"
    gridref "SO1234"

## References

* [http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid](http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid)
* [http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf](http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf)

## License

Copyright © 2013 Matt Walker (walkermatt@longwayaround.org.uk)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
