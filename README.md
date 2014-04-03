# GridRef

Clojure CLI and functions to convert an alpha numeric Ordnance Survey grid reference to easting / northing or easting / northing to a grid reference.

Valid input includes:

    ST                    => [300000.0 100000.0] (The bottom right coordinate of the ST grid square)
    NN1665071250          => [216650.0 771250.0] (The Ben Nevis)
    SU387148              => [438700.0 114800.0] (The Ordnance Survey offices in Southampton)
    SU31NE                => [435000.0 115000.0] (The 5 km square of the OS office)
    TQ336805              => [533600.0 180500.0] (The Tower of London)

    "[300000.0 100000.0]" => ST (The bottom right coordinate of the ST grid square)
    "216650.0 771250.0"   => NN1665071250 (The Ben Nevis)
    "[438700 114800]"     => SU387148 (The Ordnance Survey offices in Southampton)
    "533600 180500"       => TQ3360080500 (The Tower of London)

The `gridref.core` namespace defines the functions `gridref2coord` and `coord2gridref` that support converting between a grid reference and coordinate pair.

## CLI

### Build

Run the following to build a standalone executable `target/gridref` using [Leiningen](https://github.com/technomancy/leiningen):

    $ lein bin

### CLI Usage

Using the standalone executable:

    $ gridref <gridref>

or

    $ gridref [--figures=<n>] <coordinate>

For full usage and examples see the file `resources/cli-usage`.

## Clojure Library

The `gridref.core` namespace defines the functions `gridref2coord` and `coord2gridref` that support converting between a grid reference and coordinate pair. There are also functions to parse input `parse-gridref` and `parse-coord` which can help with cleaning up input.

### Installation

`gridref` is available as a Maven artifact from [Clojars](http://clojars.org/gridref). To use `gridref` as a library in a Clojure project you can add this in your `project.clj` with leiningen:

```clojure
[gridref "0.1.4"]
```

## Todo

* Input and output in WTK or GeoJSON?

## References

* [http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid](http://en.wikipedia.org/wiki/Ordnance_Survey_National_Grid)
* [http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf](http://www.ordnancesurvey.co.uk/docs/support/national-grid.pdf)

## License

Copyright © 2013 Matt Walker (walkermatt@longwayaround.org.uk, @_walkermatt on Twitter)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
