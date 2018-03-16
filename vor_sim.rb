# VORsim v1.0

print "X: "
x = gets
print "Y: "
y = gets
pm = PilotMath.new(x,y)
pm.getQuad(x,y)


class PilotMath

  def initialize(x,y)
    @x = x
    @y = y
  end

  def to_degrees(radians)
    return radians * 180 / Math::PI
  end

  def getQuad(x, y)
    s = ""
    vor_x = 0
    voy_y = 0
    vor_01_location = [vor_x,vor_y]
    if y < vor_y
      s = "S"
    elsif y > vor_y
      s = "N"
    end

    if x < vor_x
      s = s + "W"
    elsif x > vor_x
      s = s + "E"
    end

    return s

  end

  def getAzimuth(x, y, s)
    deg = x / y
    azimuth = 0
    offset = Math.atan(deg).to_degrees

    case s
    when "N"
      azimuth = 360
    when "E"
      azimuth = 90
    when "S"
      azimuth = 180
    when "W"
      azimuth = 270
    when "NE"
      azimuth = offset
    when "SE"
      azimuth = 180 + offset
    when "SW"
      azimuth = 180 + offset
    when "NW"
      azimuth = 360 + offset
    end

  end

end
