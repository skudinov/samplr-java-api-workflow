package samplr.onboarding.model;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingApplication {
    private String insurancePackage;
    private String address;
    private String broker;
}
