package kscience.plotly

import kotlinx.html.*

interface PlotlyRenderer {
    fun FlowContent.renderPlot(
        plot: Plot,
        plotId: String = plot.toString(),
        config: PlotlyConfig = PlotlyConfig()
    ): Plot
}

object StaticPlotlyRenderer : PlotlyRenderer {
    override fun FlowContent.renderPlot(plot: Plot, plotId: String, config: PlotlyConfig): Plot {
        div {
            id = plotId
            script {
                val tracesString = plot.data.toJsonString()
                val layoutString = plot.layout.toJsonString()
                unsafe {
                    //language=JavaScript
                    +"""
                        Plotly.react(
                            '$plotId',
                            $tracesString,
                            $layoutString,
                            $config
                        );
                    """.trimIndent()
                }
            }
        }
        return plot
    }
}

fun FlowContent.plot(
    plot: Plot,
    plotId: String = plot.toString(),
    config: PlotlyConfig = PlotlyConfig(),
    renderer: PlotlyRenderer = StaticPlotlyRenderer
): Plot = with(renderer) {
    renderPlot(plot, plotId, config)
}

fun FlowContent.plot(
    plotId: String? = null,
    config: PlotlyConfig = PlotlyConfig(),
    renderer: PlotlyRenderer = StaticPlotlyRenderer,
    builder: Plot.() -> Unit
): Plot {
    val plot = Plot().apply(builder)
    return plot(plot, plotId ?: plot.toString(), config, renderer)
}
