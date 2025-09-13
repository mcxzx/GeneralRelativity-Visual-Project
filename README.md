Copyright Zhihao Xu(mcxzx) 2025 under GNU Licence; Program version released year: 2022

# What is This
A software/engine written in all java (with Aparapi API for enabling multithread parallel computation and GPU acceleration(only within the same coordinate chart)), 
aimed for simulations/rendering of physically accurate visuals in general curved 4-dimension spacetimes.


![A screenshot of a simulated blackhole](/assets/SoftwareScreenshot%20(1).png)
> A screenshot of a simulated blackhole

Once there was a joke: a numerical relativity (finite-element) simulation graduate student always liked to share some nice pictures on computational relativity seminars--
 For example: this is the scenery of two blackholes emerges -- one sees two orange discs start surrounds each other, twisted, and broke into many parts.
 Nobody could say it is not enjoyable... until someone asks:

 
 > "what exactly does this picture represents"\
 > "... well, that's just a visualization of two blackholes emerging"\
 > "then what does it _physically_ visualizes?"\
 You see the problem? ~~Not quite~~\
\
Yes! Relativity is relative! Space-time division is relative and observer dependent! The "Space" and the "Moment" in the simulation is just verbatim transfer of the 3+1 space-time decomposition coordinate his finite-element simulation happens to use.
 In actual cases, all valid 3+1 decomposition of spacetime have space slice being "spacelike"(i.e. equal-time-hypersurface has postive definite metric restriction), but in actual cases a physical observer(like you!) sees the lights from the past comming towards its locus of observation, in this case, the space one "see" is at most light-like(metric restriction degerates).
 If one can really sees a space-slice employeed by 3+1 decomposition, it means that guy can see signal travelling faster than light! Which was exactly the case for that unfortunate roasted graduate!
 He made all that relativity visualization without wrapping his head around relativity.



Well, what I did here is more or less an avoidance-inspiration from that anecdote. What one sees in this program are actually physical light rays comming from the past. As people in the Academia would put:

> It renders the visual through geodesic ray tracing, which faithfully describes the geometric-optics-order approximation of electromagnetic waves coupled with most geometric-dynamical gravity theory that relies on the metric, which General Relativity and Einstein-Cartan Theory are the primary examples, and so as some class of f(R)/tensor-gravity theory.


Well, if you are already half feet out in the physics, why not make everything as authentic as possible? For that matter, the colour of this engine are specifically represented spectrally -- a literal spectral density in light frequencies, with object encoding the spectrum they emit, spectrum they absorb, and spectrum they reflect/diffuses
In this case, combined with complexified(to support polar light waves and polarizer object) 4-wave-vector directly implied in geodesic formalism and relativity-based observer frame dependence implemented, the gravitational light-frequency shift and doppler effect are simultaneously implemented(In fact, in general general relativity spacetimes, those two effects can not be isolated -- they are essentially the same!)\
<sub>When shift of spectrum is presence, the RGB representation will be no longer faithful. Thus to have physically accurate result, spectral density of light has to be enforced.</sub>


Only one unfortunate case was that the interpolating Jacobian field formalism, which represents the infinitesimal neighbour light-ray behaviors, which originally designed to work as providing first order differential of light ray propagations' terminal spectral values, and thus enabling much higher resolution with predetermined amount of direct computing pixels through interpolation with this given information of differentiation, even though had worked out mathematically, turns out to be a system of unstable second-order differential equations under runge-kutta methods. It results in quickly diverges to either infinity or goes to 0 in runtime.\
However, this was also necessary to implement a physically accurate ray's global intensity multiplier which represents directional diffusions at directions. At the time(2022) I didn't yet know exactly how, and had the luxury of time, to resolve this instability of this set of differential equations -- This however does give me an unresolved goal -- to use my current knowledge to refractor this part, or even the entire program, to allow this only unphysicality to be resolved, together with interpolation formalism in place to have much quicker and clear rendering results.(I have plenty of ideas to use many new technowledges to improve it, even the use of spherical harmonics etc. If someone pays me to do so, I promise I'll not delaying this indefinitely XD)


Even though I am not currently developing this software, the program remains a functional engine for rendering with physically accurate spacetime scenery at geometric-optic level with only one structural unphysicality given by the above one restriction.


With its all due functionality and as barebones as it looks, it was originally designed to be a fully user-oriented simulation software, with all configurations adjustable in program GUI. Due time limit at the time, this have not being realized(But now with much more ideas of human cognitions, I had much better conception of the whole GUI formulation, so it wouldn't take as much time as it originally needed to be implemented, as seen in the [Paper Prototype Demo Document](/Software%20Development%20Documentations/ZHIHAO%20XU%20-%20Paper%20Prototype%20Demo%20and%20Reflection.docx))

# Some simulation showcases
![A screenshot of the software. My poor old laptop is on fire!<sub>這就叫炎上，不是嗎？XD || since this old laptop carries a core GPU, GPU acceleration is not supported :( </sub>](/assets/SoftwareScreenshot%20(8).png)
> A screenshot of the software. My poor old laptop is on fire!<sub>這就叫炎上，不是嗎？XD || since this old laptop carries a core GPU, GPU acceleration is not supported :( </sub> 



![A screenshot of a simulated blackhole](/assets/SoftwareScreenshot%20(2).png)
![A screenshot of a simulated blackhole](/assets/SoftwareScreenshot%20(3).png)
![A screenshot of the same simulated blackhole](/assets/SoftwareScreenshot%20(4).png)
> Three screenshots of a simulated blackhole, with accretion disc placed at different radius and observe in different places



![A screenshot of the same simulated blackhole, only now higher-resolutioned](/assets/SoftwareScreenshot%20(5).png)
> A screenshot of the same simulated blackhole, only now higher-resolutioned



![A screenshot of a simulated fast(close to speed of light) moving object](/assets/SoftwareScreenshot%20(6).png)
![A screenshot of a simulated a mildly moving object(means it returns to coincide with classical newtonian spacetime philosophy)](/assets/SoftwareScreenshot%20(7).png)
> A screenshots of simulated fast and mildly moving objects(means it returns to coincide with classical newtonian spacetime philosophy)



# Usage

To know how to use this program in oneself's fullest freedom of assembling spacetime and objects, one must either consult the [Software Design Document](/Software%20Development%20Documentations/GRV%20project%20-%20Software%20Design%20Document.docx) <sub>unfortunately written completely in Microsoft Word</sub> or the source code at Main.java or Format folder(implementation of Spacetime and Objects).

## Mathematical Representation
The underlying mathematical formulation uses `class Math/Nfunction` (i.e. numerical functions) to represent functions, with a `float evaluation(float[])` method to be overridden by specific implementations. It is designed in the fullest theoretical generality; however, it is desired to have Nfunction's evaluation method being very efficient, as it will be evaluated extremely frequently during rendering. One could also write support for different interpolation of data array methods converting them into Nfunctions. If I'll be updating this project, I'll include this interface.\
Similarly, there is `class Math/Bfunction` for boolean function in float array parameters, typically designed to indicate the coordinate chart cover range.\
Tensors are represented in `class Math/Ntensor`, with all corresponding tensor operations needed implemented(such as gradient, inverse, Levi-Civita Symbol, etc. Many more detailed tensor constant/operations are implemented in `class Tensor/Tensor`).

## Colo(u)r Representation
The colours are represented by a spectral density function over strength, as implmented in `class WColor/WColor`, with also implementation of conversion from spectral density to RGB colours according to human's cone cell's spectral response curve.\
This spectral density, unlike Nfunction, due to demand of concrete calculation, is casted into an underlying fixed-length array representation with linear interpolation in between.\
`WColor[]` array, typically of only length 2(but generalizes to k+k length for a complex k component vector spectral density), represents [real,imaginary] part of spectral density, which is used to introduce polar waves, with corresponding support of complex multiplications.\
Spectral filters(e.g. those in objects in the physical world which only deflect light has colour only when they absorb spectrum of lights that is not that specific colour, which works exactly as a spectral filter) is implemented in `class Wcolor/Wfunction`.

## Spacetime Representation
It is composed with a coordinate chart - transitional function approach representation of the underlying manifold and its metric. It does mean the spacetime manifold can be constructed in the fullest generality, but also means different chart representations of the same underlying spacetime may cause slower/faster runtimes.\
Coordinate charts are implemented in `class Format/Region`, with all relavant transitional function, metric and object list included. Each `Region`(i.e. charts) are uniquely numbered by an integer(called `Region.code`), and transitional functions in the array's `Bfunction[]`/`Nfunction[]`(`when`/`how`) index `i`'s position is understood as to chart specified by unique `code` given by `neighbor[i]`\
There is also difference functional approch to the calculation of the Christolfel Symbol automatically supported, with one able to optimize it by given oneself's better representation of it.


## Object Representation
Object are represented by `class Format/Obj` class， with all its colour emission/filter/reflection spectral information, and position/shape parameters(supports typical 3-simplex, 6-parallelogram or ellipse, which shape is governed by coordinate and is independent of metric. Specific detail see comments of [Obj.java](/src/main/java/Format/Obj.java)), and also with a unique id given by `int characterestics`\
At the time I realize, to have general curve-spacetime ray tracing work faster, a hierarchy of objects is necessary in order for bounding box checking to ignore irrelavant information. Thus Objects (`Format/Obj`) are organized in folder structure in `class Format/ObjFolder`.\
With simple calculations, it is found that with `N` objects in a coordinate chart, it is best that each `ObjFolder` contains hierarchically weighted average of `e≈2.71` entries of subfolders until the objects themselves, and it can reduce box comparing time from `O(N)` to `O(log(N))`.





## Note
This is a follow-up written ReadMe after three years in the purpose of explaining and commenting this old project, with also a fullest desire trying to be _employeed_! Yes, not anything else, but exactly that!





