# GridRef

Clojure CLI and functions to convert an alpha numeric Ordnance Survey grid reference to easting / northing or easting / northing to a grid reference.

Valid input includes:

    ST                    => [300000.0 100000.0] (The bottom right coordinate of the ST grid square)
    NN1665071250          => [216650.0 771250.0] (The Ben Nevis)
    SU387148              => [438700.0 114800.0] (The Ordnance Survey offices in Southampton)
    TQ336805              => [533600.0 180500.0] (The Tower of London)

    "[300000.0 100000.0]" => ST (The bottom right coordinate of the ST grid square)
    "216650.0 771250.0"   => NN1665071250 (The Ben Nevis)
    "[438700 114800]"     => SU387148 (The Ordnance Survey offices in Southampton)
    "533600 180500"       => TQ3360080500 (The Tower of London)

The `gridref.core` namespace defines the functions `gridref2coord` and `coord2gridref` that support converting between a grid reference and coordinate pair.

## Build

Using [Leiningen](https://github.com/technomancy/leiningen):

    $ lein uberjar

## CLI Usage

Using the lein run command:

    $ lein run <gridref>

or

    $ lein run [--figures=<n>] <coordinate>

Using the standalone jar:

    $ java -jar gridref.jar <gridref>

or

    $ java -jar gridref.jar [--figures=<n>] <coordinate>

For full usage and examples see the file ./resources/cli-usage.

## Todo

* Exit with 1 on error
* Input and output in WTK or GeoJSON?
* Handle NE, SW etc. suffixes?

## References

* [http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid](http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid)
* [http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf](http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf)

## License

Copyright © 2013 Matt Walker (walkermatt@longwayaround.org.uk, @_walkermatt on Twitter)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
