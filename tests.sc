val x = """ <script type="text/javascript">
  |//<![CDATA[
  |
  |googletag.cmd.push(function() { googletag.display('div-gpt-ad-1333485666094-3'); });
  |
  |//]]>
  </script>""".stripMargin

val s: String =
  """hi there
    //|<![CDATA[owier
    |oiwje
    |//r]]> blabla""".stripMarginxs.replaceAll((?s)"<!\\[CDATA\\[.*?\\]\\]>", "")