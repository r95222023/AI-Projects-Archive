# College Major Recommendation System

Choosing a college major can be a complex decision for many students. Research indicates that more than half of college graduates would opt for a different major if given the opportunity. While economic factors often influence this decision, personal values and traits play a crucial role as well. This system is designed to assist students in selecting a major that best matches their abilities, preferences, and career goals.

Built upon Netica, this system leverages Bayesian networks to assess and score various majors based on the user's input. A higher score for a major indicates a better alignment with the student's profile.

## Task

1. **Input Gathering**: Collect data from the user regarding their abilities, coursework, and preferences.
2. **Probability Calculation**: Compute the probability for each major based on the provided data.
3. **Scoring**: Assign scores to each major according to the user's preferences. The major with the highest score is considered the best fit.

## Inputs

### Abilities
- **Logics**: Ability to reason logically. (Great / Bad)
- **Verbal**: Ability to express in words. (Great / Bad)
- **Teamwork**: Ability to work effectively with others. (Great / Bad)
- **Leadership**: Ability to motivate and lead a group. (Great / Bad)
- **Creativity**: Ability to generate imaginative ideas. (Great / Bad)
- **Aesthetics**: Ability to appreciate the arts. (Great / Bad)
- **SpacialAbility**: Ability to understand spatial relations. (Great / Bad)
- **MemoryAttributes**: Ability to remember things. (Great / Bad)

### Courses
- **CoursePhys**: Performance in physics courses (e.g., general physics, thermodynamics). (Great / Bad)
- **CourseMath**: Performance in math courses (e.g., linear algebra, calculus). (Great / Bad)
- **CourseCS**: Performance in computer science courses (e.g., data science, AI). (Great / Bad)
- **CourseChem**: Performance in chemistry courses (e.g., general chemistry, organic chemistry). (Great / Bad)
- **CourseBio**: Performance in biology courses (e.g., biology, biochemistry). (Great / Bad)
- **CourseArt**: Performance in art courses (e.g., modern art, classical music). (Great / Bad)

### Preferences
- **Preferences**: Weight each major according to personal preference. Higher values indicate stronger preferences. The sum of all preference values should equal 1.
- **ExpectedIncome**: Future income expectation. (High / Low)

## Non-Inputs

### Attributes
- **MemoryAttributes**: Ability to remember things.
- **ThinkingReasoningAttributes**: Ability to think and reason.
- **SocialAttributes**: Ability to work with people.
- **ArtAttributes**: Ability to make art.

### Probabilities
- **Physics**: Probability percentage of students in physics-related majors.
- **Math**: Probability percentage of students in math-related majors.
- **ComputerScience**: Probability percentage of students in computer science-related majors.
- **Medical**: Probability percentage of students in medical-related majors.
- **Dental**: Probability percentage of students in dental-related majors.
- **Business**: Probability percentage of students in business-related majors.
- **Art**: Probability percentage of students in art-related majors.

## Usage

1. Unzip the file and open `MajorFinder_v2.neta` in Netica (version 5.52 or later is recommended).
2. Compile the network by clicking the Compile button (lightning icon) if it isn't already compiled.
3. Enjoy exploring your college major options!