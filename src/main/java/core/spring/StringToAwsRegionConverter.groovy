package core.spring

import org.springframework.core.convert.converter.Converter
import software.amazon.awssdk.regions.Region

class StringToAwsRegionConverter implements Converter<String, Region> {

    @Override
    Region convert(String source) {
        String regionId = source.toLowerCase(Locale.ENGLISH).replaceAll('_', '-')
        Optional.ofNullable(Region.regions().find { region -> regionId == region.id() }).orElse(Region.of(regionId))
    }
}
